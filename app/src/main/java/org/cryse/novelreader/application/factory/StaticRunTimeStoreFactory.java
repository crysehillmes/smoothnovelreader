package org.cryse.novelreader.application.factory;

import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.store.HashTableRunTimeStore;

public enum StaticRunTimeStoreFactory {
    HASHTABLE(HashTableRunTimeStore.getInstance());
    RunTimeStore runTimeStore;

    StaticRunTimeStoreFactory(RunTimeStore runTimeStore) {
        this.runTimeStore = runTimeStore;
    }

    public static RunTimeStore getInstance() {
        return HASHTABLE.getValue();
    }

    private RunTimeStore getValue() {
        return runTimeStore;
    }
}
