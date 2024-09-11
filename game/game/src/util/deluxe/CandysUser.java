package util.deluxe;

import util.User;

public class CandysUser extends User {
    public Candys2Data candys2Data;
    public Candys3Data candys3Data;

    public CandysUser(String username) {
        super(username);
        candys2Data = new Candys2Data();
        candys3Data = new Candys3Data();
    }

    public void setUser(CandysUser user){
        super.setUser(user.username);
        if (user.candys3Data != null) candys3Data.update(user.candys3Data);
    }

    public void loadOldData(String response){
        candys3Data = new Candys3Data();
        String[] data = response.split("\\|");
        candys3Data.mainCastStar = Integer.parseInt(data[0]);
        candys3Data.shadowCastStar = Integer.parseInt(data[1]);
        System.out.println("Data loaded!");
    }
}