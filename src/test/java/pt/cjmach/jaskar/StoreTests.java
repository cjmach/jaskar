/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pt.cjmach.jaskar;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author mach
 */
public class StoreTests {

    @Test
    public void givenOpenStore_whenInsertingAndFetchingKey_thenKeysAreEqual() {
        try {
            String passKey = Store.generateRawKey("");
            Key keyPair = Key.generate(KeyAlgorithm.ED25519, KeyBackend.SOFTWARE, false);
            try (Store db = Store.provision("sqlite://:memory:", StoreKeyMethod.RAW, passKey, null, true); Session conn = db.openSession()) {

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
    public void givenOpenStore_whenCopyingToAnotherStore_thenCopiedKeysAreEqual() {
        try {
            String passKey = Store.generateRawKey("");
            Key keyPair = Key.generate(KeyAlgorithm.ED25519, KeyBackend.SOFTWARE, false);
            try (Store db = Store.provision("sqlite://:memory:", StoreKeyMethod.RAW, passKey, null, true); Session conn = db.openSession()) {

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
                Store dbCopy = db.copyTo("sqlite://:memory:", StoreKeyMethod.RAW, passKeyCopy, true);
                Session connCopy = dbCopy.openSession();

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
                
                Entry foundEntry = conn.fetch(rowCategory, rowName, false);
                try {
                    assertNotNull(foundEntry, "Error loading row");
                } finally {
                    if (foundEntry != null) {
                        foundEntry.close();
                    }
                }
            }
        } catch (AskarException | IOException ex) {
            fail(ex);
        }
    }
}
