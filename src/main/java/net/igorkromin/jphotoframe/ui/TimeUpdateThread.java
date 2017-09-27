package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.ConfigOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUpdateThread extends DataUpdateThread {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public TimeUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime) {
        super(controller, config, data, sleepTime);

        dateFormat = new SimpleDateFormat(config.getDateFormat());
        timeFormat = new SimpleDateFormat(config.getTimeFormat());
    }

    @Override
    public void doUpdate() {
        Date now = new Date();
        ModelData data = getData();

        String date = dateFormat.format(now);
        String time = timeFormat.format(now);

        // only update the date/time if it's actually changed
        if (!date.equals(data.getDateString()) || !time.equals(data.getTimeString())) {
            data.setDateTime(date, time);
            getController().requestUpdate();
        }
    }

}
