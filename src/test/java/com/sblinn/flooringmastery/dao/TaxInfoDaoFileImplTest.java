
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author sarablinn
 */
public class TaxInfoDaoFileImplTest {    
    
    private TaxInfoDao testTaxInfoDao;
    private static String TEST_FILE;
    
    public TaxInfoDaoFileImplTest() {
//        this.testTaxInfoDao = new TaxInfoDaoFileImpl();
               
        ApplicationContext appContext = 
                new ClassPathXmlApplicationContext("testApplicationContext.xml");
        testTaxInfoDao = appContext.getBean("taxInfoDao", TaxInfoDao.class);
        TEST_FILE = appContext.getBean("testTaxesFile", String.class);
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() throws Exception {
        //new FileWriter(TEST_FILE);
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        // set up and clear the taxInfoDao test file so it is empty
        // before each test
        new FileWriter(TEST_FILE);
        // might throw IOException or fileNotFoundException
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        new FileWriter(TEST_FILE);
    }

    
    @Test
    public void testCreateAndGetTaxInfo() throws Exception {
        // create a TaxInfo 
        TaxInfo taxInfo = new TaxInfo();
        taxInfo.setStateAbbr("MN");
        taxInfo.setStateName("Minnesota");
        taxInfo.setTaxRate(new BigDecimal("6.875"));
        
        // Add it to the DAO
        TaxInfo createdTaxInfo = testTaxInfoDao
                .createTaxInfo(taxInfo.getStateAbbr(), taxInfo);
        // Get it from the DAO
        TaxInfo retrievedTaxInfo = testTaxInfoDao
                .getTaxInfo(taxInfo.getStateAbbr());
        
        // Check that the retrieved TaxInfo is the same as the created one
        assertEquals(taxInfo, retrievedTaxInfo,
                "TaxInfo retrieved from DAO should be the same as the one created.");
    }
    
    @Test
    public void testGetAllTaxInfos() throws Exception {
        // create two TaxInfos     
        TaxInfo taxInfoOne = new TaxInfo("MN", "Minnesota", new BigDecimal("6.875"));
        TaxInfo taxInfoTwo = new TaxInfo("ND", "North Dakota", new BigDecimal("5.0"));              
        // add both to the DAO
        testTaxInfoDao.createTaxInfo("MN", taxInfoOne);
        testTaxInfoDao.createTaxInfo("ND", taxInfoTwo);
        // get the list of taxInfo from the DAO
        List<TaxInfo> taxInfoList = testTaxInfoDao.getAllTaxInfo();
        
        // check that the list != null && contains exactly 2 items
        assertNotNull(taxInfoList, "Checking that the list is not null.");
        assertEquals(2, taxInfoList.size(), "Checking that the list has exactly 2 items.");
        
        assertTrue(taxInfoList.contains(taxInfoOne));
        assertTrue(taxInfoList.contains(taxInfoTwo));
    }
    
    @Test
    public void testUpdateTaxInfo() throws Exception {
        // create a taxInfo
        BigDecimal firstTaxRate = new BigDecimal("6.875");
        BigDecimal secondTaxRate = new BigDecimal("2.5");
        TaxInfo taxInfo = new TaxInfo("MN", "Minnesota", firstTaxRate);
        // add it to the DAO
        testTaxInfoDao.createTaxInfo("MN", taxInfo);
        // modify it and update in the DAO
        taxInfo.setTaxRate(secondTaxRate);
        testTaxInfoDao.updateTaxInfo("MN", taxInfo);
        TaxInfo updatedTaxInfo = testTaxInfoDao.getTaxInfo("MN");
        
        // check that updatedTaxInfo is not null 
        assertNotNull(updatedTaxInfo);
        // check that updatedTaxInfo's taxRate is 2.5
        assertTrue(updatedTaxInfo.getTaxRate().compareTo(secondTaxRate) == 0,
                "Checking that updated taxRate for test TaxInfo equals 2.5.");
    }
    
    @Test
    public void testDeleteTaxInfo() throws Exception {
        // create a taxInfo
        TaxInfo taxInfo = new TaxInfo("MN", "Minnesota", new BigDecimal("6.875"));
        // add it to the DAO
        testTaxInfoDao.createTaxInfo("MN", taxInfo);
        // delete it from the DAO
        testTaxInfoDao.deleteTaxInfo("MN");
        // get a list of all taxInfo in the DAO
        List<TaxInfo> taxInfoList = testTaxInfoDao.getAllTaxInfo();
        
        // check that the list is empty, since there was only one item in it
        assertTrue(taxInfoList.isEmpty(), "Checking that list of TaxInfo is empty.");      
    }
    
}
