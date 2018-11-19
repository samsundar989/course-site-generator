package cg.transactions;

import jtps.jTPS_Transaction;
import cg.data.CGData;
import cg.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class AddTA_Transaction implements jTPS_Transaction {
    CGData data;
    TeachingAssistantPrototype ta;
    
    public AddTA_Transaction(CGData initData, TeachingAssistantPrototype initTA) {
        data = initData;
        ta = initTA;
    }

    @Override
    public void doTransaction() {
        data.addTA(ta);        
    }

    @Override
    public void undoTransaction() {
        data.removeTA(ta);
    }
}
