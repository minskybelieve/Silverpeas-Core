/*
 * Copyright (C) 2000 - 2016 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.core.calendar.event.notification;

import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.calendar.event.Attendee;
import org.silverpeas.core.notification.user.UserNotification;
import org.silverpeas.core.notification.user.client.constant.NotifAction;

/**
 * A notifier of attendees about their participation in a calendar event. It listens for change
 * in the attendance and then, for each change, it notifies the concerned attendee(s) about it.
 * <p>
 * When a calendar event is planned in a given calendar or when such an event
 * is unplanned then a change is also occurring in the attendance as the attendees are all
 * respectively just added or just deleted. This notifier will then also inform such of
 * lifecycle events.
 * @author mmoquillon
 */
public class CalendarEventAttendeeNotifier extends AttendeeNotifier<AttendeeLifeCycleEvent> {

  /**
   * An attendee has been removed. The attendee is informed about it (he shouldn't be the user
   * behind this attendance deletion).
   * @param event the lifecycle event on the deletion of an attendance.
   * @throws Exception if an error occurs while notifying the attendee.
   */
  @Override
  public void onDeletion(final AttendeeLifeCycleEvent event) throws Exception {
    Attendee attendee = event.getTransition().getBefore();
    UserNotification notification =
        new CalendarEventAttendeeNotificationBuilder(attendee.getEvent(), NotifAction.UPDATE).from(
            User.getCurrentRequester())
            .to(attendee)
            .about(UpdateCause.ATTENDEE_REMOVING, attendee)
            .build();
    notification.send();
  }

  /**
   * An attendee has been updated:
   * <ul>
   * <li>The presence status has been updated: the concerned attendee is informed about it (he
   * shouldn't be the user behind this status change).</li>
   * <li>The participation status has been updated: all the others attendees are informed about
   * it.</li>
   * </ul>
   * @param event the lifecycle event on the update of a resource.
   * @throws Exception if an error occurs while notifying the attendee.
   */
  @Override
  public void onUpdate(final AttendeeLifeCycleEvent event) throws Exception {
    Attendee before = event.getTransition().getBefore();
    Attendee after = event.getTransition().getAfter();
    if (before.getPresenceStatus() != after.getPresenceStatus()) {
      UserNotification notification =
          new CalendarEventAttendeeNotificationBuilder(after.getEvent(), NotifAction.UPDATE).from(
              User.getCurrentRequester())
              .to(after)
              .about(UpdateCause.ATTENDEE_PRESENCE, after)
              .build();
      notification.send();
    } else {
      UserNotification notification =
          new CalendarEventAttendeeNotificationBuilder(after.getEvent(), NotifAction.UPDATE).from(
              User.getCurrentRequester())
              .to(concernedAttendeesIn(after.getEvent()))
              .about(UpdateCause.ATTENDEE_PARTICIPATION, after)
              .build();
      notification.send();
    }
  }

  /**
   * An attendee has been added among the attendees. The added attendee is
   * informed about it (he shouldn't be the user behind this attendance adding).
   * @param event the lifecycle event on the adding of the attendee.
   * @throws Exception if an error occurs while notifying the attendee.
   */
  @Override
  public void onCreation(final AttendeeLifeCycleEvent event) throws Exception {
    Attendee attendee = event.getTransition().getAfter();
    UserNotification notification =
        new CalendarEventAttendeeNotificationBuilder(attendee.getEvent(), NotifAction.UPDATE).from(
            User.getCurrentRequester())
            .to(attendee)
            .about(UpdateCause.ATTENDEE_ADDING, attendee)
            .build();
    notification.send();
  }
}
