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
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ErrorCode;

/**
 *
 * @author cmachado
 */
public class EntryList implements Closeable {

    private Pointer handle;
    private final int size;

    /**
     * 
     * @param handle
     * @throws AskarException 
     */
    EntryList(Pointer handle) throws AskarException {
        this(handle, listCount(handle));
    }

    /**
     * 
     * @param handle
     * @param size 
     */
    private EntryList(Pointer handle, int size) {
        this.handle = handle;
        this.size = size;
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
     * 
     * @param index
     * @return 
     */
    public Entry get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return new Entry(handle, index);
    }

    /**
     * 
     * @return 
     */
    public int size() {
        return size;
    }

    /**
     * 
     * @return 
     */
    public Entry[] toArray() {
        Entry[] entries = new Entry[size];
        for (int i = 0; i < size; i++) {
            entries[i] = get(i);
        }
        return entries;
    }

    /**
     * 
     * @param handle
     * @return
     * @throws AskarException 
     */
    static int listCount(Pointer handle) throws AskarException {
        IntByReference out = new IntByReference();
        ErrorCode errorCode = AskarLibrary.askar_entry_list_count(handle, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        int count = out.getValue();
        return count;
    }
}
