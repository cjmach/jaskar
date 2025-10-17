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
package pt.cjmach.jaskar.lib;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author cmachado
 */
public class StringList implements Closeable {

    private Pointer handle;

    /**
     *
     * @param handle
     */
    public StringList(Pointer handle) {
        this.handle = handle;
    }
    
    /**
     *
     */
    @Override
    public void close() {
        if (handle != Pointer.NULL) {
            AskarLibrary.askar_string_list_free(handle);
            handle = Pointer.NULL;
        }
    }

    /**
     *
     * @return
     */
    public int count() {
        IntByReference out = new IntByReference();
        ErrorCode errorCode = AskarLibrary.askar_string_list_count(handle, out);
        if (errorCode == ErrorCode.SUCCESS) {
            int result = out.getValue();
            return result;
        }
        return 0;
    }

    String getItem(int i) {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_string_list_get_item(handle, i, out);
        if (errorCode == ErrorCode.SUCCESS) {
            Pointer p = out.getValue();
            String result = p.getString(0, StandardCharsets.US_ASCII.name());
            Native.free(Pointer.nativeValue(p));
            return result;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String[] toArray() {
        String[] result = new String[count()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getItem(i);
        }
        return result;
    }
}
