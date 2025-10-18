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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SecretBuffer;

/**
 * A record in the store.
 * 
 * @author cmachado
 */
public class Entry implements Closeable {

    private Pointer handle;
    private final int position;

    /**
     * 
     * @param handle
     * @param position 
     */
    Entry(Pointer handle, int position) {
        this.handle = handle;
        this.position = position;
    }

    /**
     * 
     */
    @Override
    public void close() {
        if (handle != Pointer.NULL) {
            AskarLibrary.askar_entry_list_free(handle);
            handle = Pointer.NULL;
        }
    }

    /**
     * Gets the category of the entry record.
     * 
     * @return
     * @throws AskarException 
     */
    public String getCategory() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_entry_list_get_category(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer value = out.getValue();
        String result = value.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(value));
        return result;
    }

    /**
     * Gets the name of the entry record, unique within its category.
     * 
     * @return
     * @throws AskarException 
     */
    public String getName() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_entry_list_get_name(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer value = out.getValue();
        String result = value.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(value));
        return result;
    }

    /**
     * 
     * @return
     * @throws AskarException 
     */
    SecretBuffer getRawValue() throws AskarException {
        SecretBuffer buffer = new SecretBuffer();
        ErrorCode errorCode = AskarLibrary.askar_entry_list_get_value(handle, position, buffer);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        return buffer;
    }

    /**
     * Gets the tags associated with the entry record.
     * 
     * @return
     * @throws AskarException 
     */
    public String getTags() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_entry_list_get_tags(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer value = out.getValue();
        String result = value.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(value));
        return result;
    }

    /**
     * Gets the value of the entry record.
     * 
     * @return
     * @throws AskarException 
     */
    public byte[] getValue() throws AskarException {
        try (SecretBuffer buffer = getRawValue()) {
            byte[] result = buffer.getBytes();
            return result;
        }
    }
}
