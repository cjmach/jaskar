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

/**
 *
 * @author cmachado
 */
public class KeyEntry implements Closeable {

    private Pointer handle;
    private final int position;

    /**
     * 
     * @param handle
     * @param position 
     */
    KeyEntry(Pointer handle, int position) {
        this.handle = handle;
        this.position = position;
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

    /**
     * 
     * @return
     * @throws AskarException 
     */
    public KeyAlgorithm getAlgorithm() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_entry_list_get_algorithm(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String algorithm = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return KeyAlgorithm.fromAlgorithm(algorithm);
    }
    
    /**
     * 
     * @return
     * @throws AskarException 
     */
    public String getMetaData() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_entry_list_get_metadata(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String metaData = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return metaData;
    }

    /**
     * 
     * @return
     * @throws AskarException 
     */
    public String getName() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_entry_list_get_name(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String name = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return name;
    }
    
    /**
     * 
     * @return
     * @throws AskarException 
     */
    public String getTags() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_entry_list_get_tags(handle, position, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String tags = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return tags;
    }
}
