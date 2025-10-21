/*
 *  Copyright 2025 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.jaskar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.IOException;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ErrorCode;

/**
 *
 * @author cmachado
 */
final class ErrorMessage {

    public int code;
    public String message;

    /**
     * 
     * @return 
     */
    static ErrorMessage create() {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_get_current_error(out);
        if (errorCode != ErrorCode.SUCCESS) {
            return null;
        }
        Pointer p = out.getValue();
        String jsonMessage = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));

        ObjectMapper mapper = new JsonMapper();
        try {
            ErrorMessage errorMessage = mapper.readValue(jsonMessage, ErrorMessage.class);
            return errorMessage;
        } catch (IOException ex) {
            return null;
        }
    }
}
