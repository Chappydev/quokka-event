package com.example.quokka_event.models.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quokka_event.R;
import java.util.List;

/**
 * Adapter class for displaying a list of events in a RecyclerView.
 * Binds each event to a row in the RecyclerView and handles click events.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnEventClickListener listener;

    /**
     * Constructor for EventAdapter.
     * @author Soaiba
     * @param eventList List of Event objects to be displayed
     * @param listener Listener for handling item click events
     */
    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder to represent an event.
     * Inflates the item layout for each event in the list.
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup,int)">RecyclerView.Adapter#onCreateViewHolder</a>
     * @author Soaiba
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return A new EventViewHolder that holds a View for each event
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Displays the data at the specified position.
     * Updates the contents of the ViewHolder to show the event at the given position.
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#bindViewHolder(VH,int)">RecyclerView.Adapter#onBindViewHolder</a>
     * @author Soaiba
     * @param holder The ViewHolder which should be updated
     * @param pos The position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int pos) {
        Event event = eventList.get(pos);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate().toString());
        //TODO figure out status
        holder.eventStatus.setText(event.isDeadline() ? "Closed" : "Open"); //how do you actually get the status?
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @author Soaiba
     * @return The number of items in the event list
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class that holds references to each item's views.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventStatus;

        /**
         * Constructor for EventViewHolder.
         * @author Soaiba
         * @param itemView The item view layout that contains the TextViews for event data
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventStatus = itemView.findViewById(R.id.event_status);
        }
    }

    /**
     * Interface for handling clicks on each event item.
     */
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
