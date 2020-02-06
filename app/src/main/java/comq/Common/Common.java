package comq.Common;

public class Common {
//    public static Model currentUser;
//
//    public static StaffUser staffUser;

    public static final String UPDATE = "Update";
    public static final String CANCEL = "Cancel";
    public static final String DELETE = "Delete";
    public static final String APPROVE = "Approve";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String converCodeToStatus(String code){
        if (code.equals("0")){
            return "Placed";
        }
        else  if (code.equals("1")){
            return "On my way";
        }
        else {
            return "Shipped";
        }

    }




}
