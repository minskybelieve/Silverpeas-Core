/*
 * Copyright (C) 2000 - 2014 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.comment.service.notification;

import com.silverpeas.comment.model.Comment;
import org.silverpeas.notification.JMSResourceEventNotifier;
import org.silverpeas.notification.ResourceEvent;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Topic;

/**
 * A notifier of events about the comments. The notifications are sent asynchronously by JMS to
 * the dedicated topic <code>topic/comments</code>.
 * @author mmoquillon
 */
@JMSDestinationDefinitions(
    value = {@JMSDestinationDefinition(
        name = "java:/topic/comments",
        interfaceName = "javax.jms.Topic",
        destinationName = "CommentEventNotification")})
public class CommentEventNotifier extends JMSResourceEventNotifier<Comment, CommentEvent> {

  @Resource(lookup = "java:/topic/comments")
  private Topic topic;

  @Override
  protected Destination getDestination() {
    return topic;
  }

  @Override
  protected CommentEvent createResourceEventFrom(final ResourceEvent.Type type,
      final Comment... resource) {
    return new CommentEvent(type, resource);
  }
}