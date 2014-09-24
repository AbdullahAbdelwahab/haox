package org.haox.kerb.spec.type.common;

import org.haox.kerb.spec.type.KrbSequenceOfType;

/**
 -- NOTE: HostAddresses is always used as an OPTIONAL field and
 -- should not be empty.
 HostAddresses   -- NOTE: subtly different from rfc1510,
 -- but has a value mapping and encodes the same
 ::= SEQUENCE OF HostAddress
 */
public class HostAddresses extends KrbSequenceOfType<HostAddress> {

}