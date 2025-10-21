/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pt.cjmach.jaskar;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author mach
 */
public class CryptoBoxTests {
    
    @Test
    public void givenMessageAndKey_whenSealingAndUnsealing_thenMessageIsSame() {
        try (Key key = Key.generate(KeyAlgorithm.X25519, false)) {
            byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
            byte[] sealed = CryptoBox.seal(key, message);
            
            byte[] opened = CryptoBox.sealOpen(key, sealed);
            
            assertArrayEquals(opened, message);
        } catch (AskarException ex) {
            fail(ex);
        }
    }
}
