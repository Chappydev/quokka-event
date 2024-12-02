/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
// The Cloud Functions for Firebase SDK to create Cloud Functions and triggers.

const {logger} = require("firebase-functions");
const {onSchedule} = require("firebase-functions/v2/scheduler");
const {getFirestore, Timestamp} = require("firebase-admin/firestore");
const admin = require("firebase-admin");
const {initializeApp} = require("firebase-admin/app");

initializeApp();
const db = getFirestore();

exports.checkEventDeadlines = onSchedule("0 0 * * *", async (event) => {
  logger.info("Checking for events...");

  // Get all events from Firestore
  const eventsRef = db.collection("Events");
  const snapshot = await eventsRef.get();

  if (snapshot.empty) {
    logger.info("No events found.");
    return null;
  }

  const currentTime = Timestamp.now();

  // Loop through each event and check if the deadline has passed
  const tasks = snapshot.docs.map(async (doc) => {
    const eventData = doc.data();
    const deadline = eventData.deadline;

    if (deadline.toDate() <= currentTime.toDate()) {
      logger.info(`Deadline passed for event ${doc.id}. Running lottery...`);

      // Run lottery for this event
      const spots = await getAvailableSpots(doc.id);
      await runLottery(doc.id, spots);
    }
  });

  await Promise.all(tasks);
  return null;
});

/**
 * Get available spots in an event.
 * @param {string} eventId - event id of event in database
 * @param {number} slots - available spots for event
 *
 */
async function runLottery(eventId, slots) {
  logger.info(`Running lottery for event: ${eventId}`);

  const waitingUsers = await admin.firestore()
      .collection("Enrolls")
      .where("eventId", "==", eventId)
      .where("status", "==", "Waiting")
      .get();

  const results = waitingUsers.docs.map((doc) => doc.data());

  if (results.length > 0) {
    // Shuffle array
    for (let i = results.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [results[i], results[j]] = [results[j], results[i]]; // Swap elements
    }

    const winners = results.slice(0, Math.min(slots, results.length));

    winners.forEach((winner, index) => {
      logger.info(`Winner ${index + 1}: ${winner.userId}`);
    });
  } else {
    logger.info("No users available for the lottery.");
  }
}

/**
 * Get available spots in an event.
 * @param {string} eventId - event id of event in database
 * @return {number} availableSpots - number of available spot in event
 */
async function getAvailableSpots(eventId) {
  const eventSnapshot = await admin.firestore()
      .collection("Events")
      .doc(eventId)
      .get();

  if (!eventSnapshot.exists) {
    throw new Error(`Event with ID ${eventId} not found.`);
  }

  const eventData = eventSnapshot.data();
  const totalSpots = eventData.maxParticipants;

  const attendingUsers = await admin.firestore()
      .collection("Enrolls")
      .where("eventId", "==", eventId)
      .where("status", "==", "Attending")
      .get();

  const attendingCount = attendingUsers.size;

  const availableSpots = totalSpots - attendingCount;


  return availableSpots;
}

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
