package util.deluxe;

import util.User;

public class Candys2User extends User {
    public Candys2User(String username) {
        super(username);
    }

    public void setUser(Candys2User user){
        super.setUser(user.username);

    }
}
