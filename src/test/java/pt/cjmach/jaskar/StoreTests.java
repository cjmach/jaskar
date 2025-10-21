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

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author cmachado
 */
public class StoreTests {
    private Store store;
    
    @BeforeEach
    public void setupAskar() throws AskarException {
        String passKey = Store.generateRawKey();
        store = Store.provision("sqlite://:memory:", StoreKeyMethod.RAW, passKey, null, true);
    }
    
    @AfterEach
    public void closeAskar() throws AskarException {
        store.close(true);
    }
    
    @Test
    public void givenOpenStore_whenInserting_thenCountIsIncremented() {
        try (Session session = store.openSession()) {
            long count = session.count("testcat", null);
            session.insert("testcat", "testentry", "{\"tag\":\"a\"}", "test".getBytes(AskarLibrary.DEFAULT_CHARSET), -1);
            long newCount = session.count("testcat", null);
            assertEquals(count + 1, newCount);
        } catch (AskarException | IOException ex) {
            fail(ex);
        }
    }

    @Test
    public void givenOpenStore_whenInsertingAndFetchingKey_thenKeysAreEqual() {
        try {
            Key keyPair = Key.generate(KeyAlgorithm.ED25519, false);
            try (Session conn = store.openSession()) {

                String keyName = "testkey";
                String metaData = "meta";
                conn.insertKey(keyPair, keyName, metaData, null, -1);

                KeyEntry found = conn.fetchKey(keyName, false);
                try {
                    assertNotNull(found, "Row required");
                    assertEquals(found.getAlgorithm(), KeyAlgorithm.ED25519);
                    assertEquals(found.getName(), keyName);
                    assertEquals(found.getMetaData(), metaData);

                    Key key = found.loadLocal();
                    assertNotNull(key, "Error loading key");
                    assertEquals(keyPair.getJwkThumbprint(), key.getJwkThumbprint());
                } finally {
                    if (found != null) {
                        found.close();
                    }
                }
            }
        } catch (AskarException | IOException ex) {
            fail(ex);
        }
    }
    
    @Test
    public void givenOpenStore_whenRemoving_thenCountIsDecremented() {
        try (Session session = store.openSession()) {
            long countInitial = session.count("testcat", null);
            session.insert("testcat", "testentry", "{\"tag\":\"a\"}", "test".getBytes(AskarLibrary.DEFAULT_CHARSET), -1);
            long countAfterInsert = session.count("testcat", null);
            assertEquals(countInitial + 1, countAfterInsert);
            
            session.remove("testcat", "testentry");
            long countAfterRemove = session.count("testcat", null);
            assertEquals(countInitial, countAfterRemove);
        } catch (AskarException | IOException ex) {
            fail(ex);
        }
    }

    @Test
    public void givenOpenStore_whenCopyingToAnotherStore_thenCopiedKeysAreEqual() {
        try {
            Key keyPair = Key.generate(KeyAlgorithm.ED25519, false);
            try (Session conn = store.openSession()) {

                String keyName = "testkey";
                String metaData = "meta";
                conn.insertKey(keyPair, keyName, metaData, null, -1);

                String rowCategory = "testcat";
                String rowName = "testrow";
                String rowValue = "testval";

                conn.insert(rowCategory, rowName, null, rowValue.getBytes(AskarLibrary.DEFAULT_CHARSET), -1);

                String passKeyCopy = Store.generateRawKey("");
                // TODO: Using the source url as the url for the destination store 
                // is probably not correct, but it's passing and the code from 
                // https://github.com/openwallet-foundation/askar/blob/main/tests/store_copy.rs
                // works the same way.
                try (Store dbCopy = store.copyTo("sqlite://:memory:", StoreKeyMethod.RAW, passKeyCopy, true); Session connCopy = dbCopy.openSession()) {

                    KeyEntry foundKey = connCopy.fetchKey(keyName, false);
                    try {
                        assertNotNull(foundKey, "Row required");
                        assertEquals(foundKey.getAlgorithm(), KeyAlgorithm.ED25519);
                        assertEquals(foundKey.getName(), keyName);
                        assertEquals(foundKey.getMetaData(), metaData);

                        Key key = foundKey.loadLocal();
                        assertNotNull(key, "Error loading key");
                        assertEquals(keyPair.getJwkThumbprint(), key.getJwkThumbprint());
                    } finally {
                        if (foundKey != null) {
                            foundKey.close();
                        }
                    }

                    Entry foundEntry = connCopy.fetch(rowCategory, rowName, false);
                    try {
                        assertNotNull(foundEntry, "Error loading row");
                    } finally {
                        if (foundEntry != null) {
                            foundEntry.close();
                        }
                    }
                }
            }
        } catch (AskarException | IOException ex) {
            fail(ex);
        }
    }
}
