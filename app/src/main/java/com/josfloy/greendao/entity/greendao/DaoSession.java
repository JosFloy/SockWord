package com.josfloy.greendao.entity.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.josfloy.greendao.entity.greendao.wisdomEntity;
import com.josfloy.greendao.entity.greendao.CET4Entity;

import com.josfloy.greendao.entity.greendao.wisdomEntityDao;
import com.josfloy.greendao.entity.greendao.CET4EntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig wisdomEntityDaoConfig;
    private final DaoConfig cET4EntityDaoConfig;

    private final wisdomEntityDao wisdomEntityDao;
    private final CET4EntityDao cET4EntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        wisdomEntityDaoConfig = daoConfigMap.get(wisdomEntityDao.class).clone();
        wisdomEntityDaoConfig.initIdentityScope(type);

        cET4EntityDaoConfig = daoConfigMap.get(CET4EntityDao.class).clone();
        cET4EntityDaoConfig.initIdentityScope(type);

        wisdomEntityDao = new wisdomEntityDao(wisdomEntityDaoConfig, this);
        cET4EntityDao = new CET4EntityDao(cET4EntityDaoConfig, this);

        registerDao(wisdomEntity.class, wisdomEntityDao);
        registerDao(CET4Entity.class, cET4EntityDao);
    }
    
    public void clear() {
        wisdomEntityDaoConfig.getIdentityScope().clear();
        cET4EntityDaoConfig.getIdentityScope().clear();
    }

    public wisdomEntityDao getWisdomEntityDao() {
        return wisdomEntityDao;
    }

    public CET4EntityDao getCET4EntityDao() {
        return cET4EntityDao;
    }

}