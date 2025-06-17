/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package poe;

import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author RC_Student_lab
 */
public class RepoNGTest {
    
    public RepoNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of recordSent method, of class Repo.
     */
    @Test
    public void testRecordSent() {
        System.out.println("recordSent");
        Message m = null;
        Repo.recordSent(m);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of recordDisregarded method, of class Repo.
     */
    @Test
    public void testRecordDisregarded() {
        System.out.println("recordDisregarded");
        Message m = null;
        Repo.recordDisregarded(m);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of load method, of class Repo.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        String file = "";
        Repo.load(file);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class Repo.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        String file = "";
        Repo.save(file);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listSendersRecipients method, of class Repo.
     */
    @Test
    public void testListSendersRecipients() {
        System.out.println("listSendersRecipients");
        Repo.listSendersRecipients();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of showLongest method, of class Repo.
     */
    @Test
    public void testShowLongest() {
        System.out.println("showLongest");
        Repo.showLongest();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findById method, of class Repo.
     */
    @Test
    public void testFindById() {
        System.out.println("findById");
        String id = "";
        Message expResult = null;
        Message result = Repo.findById(id);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByRecipient method, of class Repo.
     */
    @Test
    public void testFindByRecipient() {
        System.out.println("findByRecipient");
        String recipient = "";
        List expResult = null;
        List result = Repo.findByRecipient(recipient);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteByHash method, of class Repo.
     */
    @Test
    public void testDeleteByHash() {
        System.out.println("deleteByHash");
        String h = "";
        boolean expResult = false;
        boolean result = Repo.deleteByHash(h);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of report method, of class Repo.
     */
    @Test
    public void testReport() {
        System.out.println("report");
        Repo.report();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
