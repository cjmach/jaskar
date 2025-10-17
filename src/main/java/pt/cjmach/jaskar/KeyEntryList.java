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

import com.sun.jna.Pointer;
import java.io.Closeable;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author cmachado
 */
public class KeyEntryList implements Closeable {
    private Pointer handle;

    /**
     * 
     * @param handle 
     */
    KeyEntryList(Pointer handle) {
        this.handle = handle;
    }
    

    /**
     * 
     */
    @Override
    public void close() {
        if (handle != Pointer.NULL) {
            AskarLibrary.askar_key_entry_list_free(handle);
            handle = Pointer.NULL;
        }
    }
}
