package madson.org.opentournament.domain;

/**
 * Pairing Options for set rules for pairing rounds of tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PairingOption {

    private String pairingOptionName;

    private boolean defaultPairing;

    private boolean active;

    /**
     * @param  pairingOptionName
     * @param  defaultPairing
     */
    public PairingOption(String pairingOptionName, boolean defaultPairing) {

        this.pairingOptionName = pairingOptionName;
        this.defaultPairing = defaultPairing;
        this.active = defaultPairing;
    }

    public String getPairingOptionName() {

        return pairingOptionName;
    }


    public boolean isDefaultPairing() {

        return defaultPairing;
    }


    public boolean isActive() {

        return active;
    }


    public void setActive(boolean active) {

        this.active = active;
    }


    @Override
    public String toString() {

        return "PairingOption{"
            + "pairingOptionName='" + pairingOptionName + '\''
            + ", defaultPairing=" + defaultPairing
            + ", active=" + active + '}';
    }
}
