
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sarablinn
 */
public class TaxInfoDaoFileImpl implements TaxInfoDao {

    private HashMap<String, TaxInfo> taxInfos = new HashMap<>();
    private final String TAXES_FILE;
    private static final String DELIMITER = ",";
    
    
    public TaxInfoDaoFileImpl() {
        this.TAXES_FILE = "../FlooringMastery/Data/StateTaxes.txt";
    }
    
    public TaxInfoDaoFileImpl(String taxesDataFilePath) {
        this.TAXES_FILE = taxesDataFilePath;
    }
    
    
    @Override
    public TaxInfo createTaxInfo(String stateAbbr, TaxInfo taxInfo) 
            throws PersistenceException {
        
        loadTaxInfos();
        
        TaxInfo newTaxInfo = taxInfos.put(stateAbbr, taxInfo);
        writeTaxInfos();
        
        return newTaxInfo;
    }

    @Override
    public TaxInfo getTaxInfo(String stateAbbr) throws PersistenceException {
        loadTaxInfos();
        
        // make input uppercase-- TaxInfo stateAbbr is always uppercase
        stateAbbr = stateAbbr.toUpperCase();
        TaxInfo taxInfo = taxInfos.get(stateAbbr);
        
        return taxInfo;
    }

    @Override
    public List<TaxInfo> getAllTaxInfo() throws PersistenceException {
        loadTaxInfos();
        
        // sort the list of tax info in alphabetical order
        List<TaxInfo> sortedTaxInfoList = new ArrayList(taxInfos.values());
        sortedTaxInfoList.sort(Comparator.comparing((t) -> t.getStateAbbr()));
        
        return sortedTaxInfoList;
    }

    @Override
    public TaxInfo updateTaxInfo(String stateAbbr, TaxInfo updatedTaxInfo) 
            throws PersistenceException {
        
        loadTaxInfos();       
        stateAbbr = stateAbbr.toUpperCase();
        taxInfos.remove(stateAbbr);
        TaxInfo retrievedTaxInfo = taxInfos.put(stateAbbr, updatedTaxInfo);
        writeTaxInfos();
        
        return retrievedTaxInfo;
    }

    @Override
    public TaxInfo deleteTaxInfo(String stateAbbr) throws PersistenceException {
        loadTaxInfos();
        
        stateAbbr = stateAbbr.toUpperCase();
        TaxInfo deletedTaxInfo = taxInfos.remove(stateAbbr);
        writeTaxInfos();
        
        return deletedTaxInfo;
    }
    
    
    private String marshallTaxInfo(TaxInfo taxInfo) {
        String taxInfoAsText = taxInfo.getStateAbbr() + DELIMITER;
        taxInfoAsText += taxInfo.getStateName() + DELIMITER;
        taxInfoAsText += taxInfo.getTaxRate();
        
        return taxInfoAsText;
    }
    
    private TaxInfo unmarshallTaxInfo(String taxInfoAsText) {
        String[] taxInfoData = taxInfoAsText.split(DELIMITER);
        
        String stateAbbr = taxInfoData[0];
        String stateName = taxInfoData[1];
        BigDecimal taxRate = new BigDecimal(taxInfoData[2]);
        
        TaxInfo taxInfoFromFile = new TaxInfo(stateAbbr, stateName, taxRate);
   
        return taxInfoFromFile;
    }
    
    private void loadTaxInfos() throws PersistenceException {
        Scanner scanner;
        
        try {
            scanner = new Scanner(
                    new BufferedReader(new FileReader(TAXES_FILE)));
        } catch(FileNotFoundException e) {
            throw new PersistenceException(
                    "Unable to load state tax data into memory.");
        }
        
        // if the file isn't empty
        if (scanner.hasNext()) {
            scanner.nextLine(); // header - ignore

            String currentLine;
            TaxInfo currentTaxInfo;

            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();
                currentTaxInfo = unmarshallTaxInfo(currentLine);
                taxInfos.put(currentTaxInfo.getStateAbbr(), currentTaxInfo);
            }
        }
        
        scanner.close();
    }
    
    private void writeTaxInfos() throws PersistenceException {
        PrintWriter out;
        
        try {
            out = new PrintWriter(new FileWriter(TAXES_FILE));
        } catch(IOException e) {
            throw new PersistenceException("Unable to save data to "
                    + "state tax data file.");
        }
        
        String header = "State,StateName,TaxRate";
        out.println(header);
        
        String taxInfoAsText;
        for (TaxInfo taxInfo : taxInfos.values()) {
            taxInfoAsText = marshallTaxInfo(taxInfo);
            out.println(taxInfoAsText);
            out.flush();
        }
        
        out.close();
    }
    
}
