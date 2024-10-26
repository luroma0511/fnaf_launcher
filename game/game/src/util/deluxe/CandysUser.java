package util.deluxe;

import com.google.gson.InstanceCreator;
import util.User;

import java.lang.reflect.Type;

public class CandysUser extends User implements InstanceCreator<CandysUser> {
    public Candys2Data candys2Data;
    public Candys3Data candys3Data;

    public CandysUser(){
        super("");
    }

    public CandysUser(String username) {
        super(username);
        candys2Data = new Candys2Data();
        candys3Data = new Candys3Data();
    }

    public void setUser(CandysUser user){
        super.setUser(user.username);
        if (user.candys2Data != null) candys2Data.update(user.candys2Data);
        if (user.candys3Data != null) candys3Data.update(user.candys3Data);
    }

    @Override
    public CandysUser createInstance(Type type) {
        return new CandysUser();
    }
}