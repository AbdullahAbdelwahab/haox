package org.haox.kdc.identitybackend;

import org.haox.kerb.identity.KrbIdentity;
import org.haox.kerb.identity.backend.AbstractIdentityBackend;

import java.util.List;

public class LdapIdentityBackend extends AbstractIdentityBackend {


    public LdapIdentityBackend() {
        super();
    }

    /**
     * Load identities from file
     */
    public void load() {
        // todo
    }

    /**
     * Persist the updated identities back
     */
    public void save() {
        // todo
    }

    @Override
    public List<KrbIdentity> getIdentities() {
        return null;
    }

    @Override
    public boolean checkIdentity(String name) {
        return false;
    }

    @Override
    public KrbIdentity getIdentity(String name) {
        return null;
    }

    @Override
    public void addIdentity(KrbIdentity identity) {

    }

    @Override
    public void updateIdentity(KrbIdentity identity) {

    }

    @Override
    public void deleteIdentity(KrbIdentity identity) {

    }
}