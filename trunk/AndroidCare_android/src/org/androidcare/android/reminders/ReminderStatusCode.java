/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.android.reminders;

/**
 * 
 * @author Alejandro Escario MŽndez
 * 
 */
// @comentario por su funcionalidad tiene sentido que esté en el paquete de reminders; la he movido
public enum ReminderStatusCode {

    ALERT_DISPLAYED(0, "ALERT DISPLAYED"), ALERT_DONE(1, "ALERT CHECKED AS DONE"), ALERT_IGNORED(2,
            "ALERT IGNORED");

    final int code;
    final String description;

    private ReminderStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }

    public static ReminderStatusCode getByCode(int codeValue) {
        for (ReminderStatusCode type : ReminderStatusCode.values()) {
            if (type.code == codeValue) {
                return type;
            }
        }
        return null;
    }

}
