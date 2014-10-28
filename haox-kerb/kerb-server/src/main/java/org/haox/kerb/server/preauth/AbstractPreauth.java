package org.haox.kerb.server.preauth;

import org.haox.kerb.preauth.PaFlag;
import org.haox.kerb.preauth.PaFlags;
import org.haox.kerb.server.KdcContext;
import org.haox.kerb.spec.KrbException;
import org.haox.kerb.spec.type.pa.PaData;
import org.haox.kerb.spec.type.pa.PaDataType;

public abstract class AbstractPreauth implements KdcPreauth {

    private KdcContext context;

    public void init(KdcContext context) {
        this.context = context;
    }

    @Override
    public void provideEData(PreauthContext preauthContext) throws KrbException {

    }

    @Override
    public void verify(PreauthContext preauthContext, PaData paData) throws KrbException {

    }

    @Override
    public void providePaData(PreauthContext preauthContext, PaData paData) {

    }

    @Override
    public PaFlags getFlags(PreauthContext preauthContext, PaDataType paType) {
        PaFlags paFlags = new PaFlags(0);
        paFlags.setFlag(PaFlag.PA_REAL);

        return paFlags;
    }

    protected KdcContext getContext() {
        return context;
    }

    @Override
    public void destroy() {

    }
}