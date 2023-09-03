package org.youcancook.gobong.global.util.service;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanupExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DatabaseCleaner databaseCleaner = getDatabaseCleaner(context);
        databaseCleaner.execute();
    }

    private DatabaseCleaner getDatabaseCleaner(ExtensionContext context){
        return SpringExtension.getApplicationContext(context).getBean(DatabaseCleaner.class);
    }
}
