package com.example.voip_app.event;


import org.greenrobot.eventbus.EventBus;

public class BusProvider {
	private static final EventBus BUS = new EventBus();
	private static final MainThreadBus MAIN_THREAD_BUS = new MainThreadBus();
	public static EventBus getBus() { return BUS; }
	public static MainThreadBus getUIBus() { return MAIN_THREAD_BUS; }
	private BusProvider() { }
}
