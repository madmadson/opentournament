package madson.org.opentournament.domain;

/**
 * Pairing Options for set rules for pairing rounds of tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PairingOption {

    private String pairingOptionName;

    private boolean defaultOption;

    private boolean active;

    /**
     * @param  pairingOptionName
     * @param  defaultOption
     */
    public PairingOption(String pairingOptionName, boolean defaultOption) {

        this.pairingOptionName = pairingOptionName;
        this.defaultOption = defaultOption;
        this.active = defaultOption;
    }

    public String getPairingOptionName() {

        return pairingOptionName;
    }


    public boolean isDefaultOption() {

        return defaultOption;
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
            + ", defaultOption=" + defaultOption
            + ", active=" + active + '}';
    }
}
