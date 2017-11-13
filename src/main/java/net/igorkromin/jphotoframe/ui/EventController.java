/**
 * JPhotoFrame - a simple Java application for displaying a collection of photos in a full-screen slideshow.
 * Copyright (C) 2015  Igor Kromin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * You can find this and my other open source projects here - http://github.com/ikromin
 */

package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.Log;
import net.igorkromin.jphotoframe.PauseListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Implements all of the event handling for the Controller class.
 */
public abstract class EventController implements KeyListener, MouseListener, PauseListener {

    View view;

    Object lock = new Object();
    boolean paused = false;

    public EventController(View view) {
        this.view = view;
        view.addKeyListener(this);
        view.addMouseListener(this);
    }

    public abstract void stop();

    protected void waitIfPaused() {
        if (paused) {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            }
            catch (InterruptedException e) {
                Log.warning("Couldn't wait on object due to error: " + e.getMessage());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Exit if any key is pressed
        stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Exit if any key is pressed
        stop();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not implemented
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Exit on mouse click
        stop();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Exit on mouse press
        stop();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // not implemented
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not implemented
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not implemented
    }

    @Override
    public void pauseEvent() {
        Log.info("Pausing");
        paused = true;
    }

    @Override
    public void resumeEvent() {
        Log.info("Resuming");
        paused = false;

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
