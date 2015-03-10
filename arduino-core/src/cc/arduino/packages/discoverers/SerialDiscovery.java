/*
 * This file is part of Arduino.
 *
 * Arduino is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction.  Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License.  This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 *
 * Copyright 2013 Arduino LLC (http://www.arduino.cc/)
 */

package cc.arduino.packages.discoverers;

import cc.arduino.packages.BoardPort;
import cc.arduino.packages.Discovery;
import processing.app.BaseNoGui;
import processing.app.Platform;
import processing.app.Serial;
import processing.app.debug.TargetBoard;
import processing.app.helpers.PreferencesMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static processing.app.I18n._;

public class SerialDiscovery implements Discovery {

  static {
    //makes transifex happy
    _("Uncertified");
  }

  @Override
  public List<BoardPort> discovery() {
    Platform os = BaseNoGui.getPlatform();
    String devicesListOutput = os.preListAllCandidateDevices();

    List<BoardPort> res = new ArrayList<BoardPort>();

    List<String> ports = Serial.list();

    for (String port : ports) {
      Map<String, Object> boardData = os.resolveDeviceAttachedTo(port, BaseNoGui.packages, devicesListOutput);

      BoardPort boardPort = new BoardPort();
      boardPort.setAddress(port);
      boardPort.setProtocol("serial");

      String label = port;

      PreferencesMap prefs = new PreferencesMap();

      if (boardData != null) {
        prefs.put("vid", boardData.get("vid").toString());
        prefs.put("pid", boardData.get("pid").toString());

        TargetBoard board = (TargetBoard) boardData.get("board");
        if (board != null) {
          String warningKey = "vid." + boardData.get("vid").toString() + ".warning";
          String warning = board.getPreferences().get(warningKey);
          prefs.put("warning", warning);

          String boardName = board.getName();
          if (boardName != null) {
            if (warning != null) {
              label += " (" + boardName + " - " + _(warning) + ")";
            } else {
              label += " (" + boardName + ")";
            }
          }
          boardPort.setBoardName(boardName);
        }
      }

      boardPort.setLabel(label);
      boardPort.setPrefs(prefs);

      res.add(boardPort);
    }
    return res;
  }

  @Override
  public void setPreferences(PreferencesMap options) {
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

}
