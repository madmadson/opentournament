package madson.org.opentournament.domain;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class ArmyList {

    private String name;
    private String list;

    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }


    public String getList() {

        return list;
    }


    public void setList(String list) {

        this.list = list;
    }


    @Override
    public String toString() {

        return "ArmyList{"
            + "name='" + name + '\''
            + ", list='" + list + '\'' + '}';
    }
}
