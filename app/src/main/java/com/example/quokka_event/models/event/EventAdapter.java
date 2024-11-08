package com.example.quokka_event.models.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This class sets up an adapter for displaying a list of events in a RecyclerView.
 * @author Soaiba
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnEventClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

    /**
     * This is a constructor for EventAdapter.
     * @author Soaiba
     * @param eventList list of Event objects to be displayed.
     * @param listener listener for handling item click events.
     */
    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    /**
     * This method creates a ViewHolder for an event.
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup,int)">RecyclerView.Adapter#onCreateViewHolder</a>
     * @author Soaiba
     * @param parent parent ViewGroup.
     * @param viewType iew type of the new View.
     * @return ViewHolder for each event.
     */
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    /**
     * This method binds the event information to the ViewHolder at a specific position.
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#bindViewHolder(VH,int)">RecyclerView.Adapter#onBindViewHolder</a>
     * @author Soaiba
     * @param holder ViewHolder to bind data to.
     * @param pos position of the event in the list.
     */
    public void onBindViewHolder(@NonNull EventViewHolder holder, int pos) {
        Event event = eventList.get(pos);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(dateFormat.format(event.getEventDate()));
        //TODO figure out status
        holder.eventStatus.setText(event.isDeadline() ? "Closed" : "Open"); //how do you actually get the status?
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    /**
     * This method returns the total number events.
     * @author Soaiba
     * @return number of events in the list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * This method holds references to each event's views.
     * @author Soaiba
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventStatus;

        /**
         * Constructor for EventViewHolder.
         * @author Soaiba
         * @param itemView layout for event.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventStatus = itemView.findViewById(R.id.event_status);
        }
    }

    /**
     * This interface is for handling clicks on each event.
     * @author Soaiba
     */
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
