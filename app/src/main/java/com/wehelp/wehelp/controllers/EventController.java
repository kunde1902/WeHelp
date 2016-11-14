package com.wehelp.wehelp.controllers;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.wehelp.wehelp.classes.Event;
import com.wehelp.wehelp.classes.Util;
import com.wehelp.wehelp.services.EventService;
import com.wehelp.wehelp.services.IServiceArrayResponseCallback;
import com.wehelp.wehelp.services.IServiceErrorCallback;
import com.wehelp.wehelp.services.IServiceResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EventController {

    EventService eventService;
    public Gson gson;
    private ArrayList<Event> listEvents;

    public Event eventTemp = null;
    public boolean errorService = false;
    public JSONObject errorMessages = null;

    public ArrayList<Event> getListEvents() {
        return this.listEvents;
    }

    public void setListEvents(ArrayList<Event> listEvents) {
        this.listEvents = listEvents;
    }

    public EventController(EventService eventService, Gson gson) {

        this.eventService = eventService;
        this.gson = gson;
    }

    public void getEvents(double lat, double lng, int perimetro) {
        this.eventService.getEvents(lat, lng, perimetro, new IServiceArrayResponseCallback() {
            @Override
            public void execute(JSONArray response) {
                setListEvents(JsonArrayToEventList(response));
            }
        }, new IServiceErrorCallback() {
            @Override
            public void execute(VolleyError error) {
                setListEvents(new ArrayList<Event>());
            }
        });
    }

    public ArrayList<Event> JsonArrayToEventList(JSONArray jsonArray) {
        ArrayList<Event> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            try {
                Event event = gson.fromJson(jsonArray.get(i).toString(), Event.class);
                list.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public Event JsonToEvent(JSONObject jsonObject) {
        return gson.fromJson(jsonObject.toString(), Event.class);
    }

    public void createEvent(Event event) throws JSONException {
        this.eventTemp = null;
        this.errorService = false;
        this.errorMessages = null;

        this.eventService.createEvent(event, new IServiceResponseCallback() {
            @Override
            public void execute(JSONObject response) {
                Log.d("WeHelpWs", response.toString());
                eventTemp = JsonToEvent(response);
            }
        }, new IServiceErrorCallback() {
            @Override
            public void execute(VolleyError error) {
                Log.d("WeHelpWS", "Error: " + error.getMessage());
                errorService = true;
                errorMessages = Util.ServiceErrorToJson(error);
            }
        });
    }
}
