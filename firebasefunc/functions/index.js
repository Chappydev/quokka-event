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
const {onRequest} = require("firebase-functions/v2/https");
const {onSchedule} = require("firebase-functions/v2/scheduler");
const {onDocumentCreated} = require("firebase-functions/v2/firestore");

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");

initializeApp();


// Check every event that has passed deadline and run lottery
exports.checkEventDeadlines = functions.pubsub.schedule('0 0 * * *')
    .timeZone('UTC')
    .onRun(async (context) => {
        console.log('Checking for events pass deadline');

        // Get all events from Firestore
        const eventsRef = admin.firestore().collection('Events');
        const snapshot = await eventsRef.get();

        if (snapshot.empty) {
            console.log('No events found.');
            return null;
        }

        const currentTime = admin.firestore.Timestamp.now();

        // Loop through each event and check if the deadline has passed
        const tasks = snapshot.docs.map(async (doc) => {
            const eventData = doc.data();
            const deadline = eventData.deadline;
            const eventId = eventData.eventId;
            if (deadline.toDate() <= currentTime.toDate()) {
                console.log(`Deadline passed for event ${eventData.eventId}. Running lottery...`);

                // Run lottery for this event
                const spots = getAvailableSpots(eventId)
                await runLottery(eventData.eventId, spots);
            }
        });

        await Promise.all(tasks);
        return null;
    });
// Function to run lottery for a given event using firebase.
async function runLottery(eventId, slots) {
    console.log(`Running lottery for event: ${eventId}`);

    const waitingUsers = await admin.firestore()
        .collection('Enrolls')
        .where('eventId', '==', eventId)
        .where('status', '==', 'Waiting')
        .get();

    const results = waitingUsers.docs.map(doc => doc.data());


    if (results.length > 0) {
        // Shuffle array
        for (let i = results.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [results[i], results[j]] = [results[j], results[i]]; // Swap elements
        }

        const winners = results.slice(0, Math.min(slots, results.length));

        winners.forEach((winner, index) => {
            console.log(`Winner ${index + 1}: ${winner.userId}`);

        });
    } else {
        console.log("No users available for the lottery.");
    }

}
// Find available spots in an event
async function getAvailableSpots(eventId) {
    const eventSnapshot = await admin.firestore()
        .collection('Events')
        .doc(eventId)
        .get();

    if (!eventSnapshot.exists) {
        throw new Error(`Event with ID ${eventId} not found.`);
    }

    const eventData = eventSnapshot.data();
    const totalSpots = eventData.maxParticipants;

    const attendingUsers = await admin.firestore()
        .collection('Enrolls')
        .where('eventId', '==', eventId)
        .where('status', '==', 'Attending')
        .get();

    const attendingCount = attendingUsers.size;

    const availableSpots = totalSpots - attendingCount;

    console.log(`Event: ${eventId}, Total spots: ${totalSpots}, Attending: ${attendingCount}, Available spots: ${availableSpots}`);

    return availableSpots;
}

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
