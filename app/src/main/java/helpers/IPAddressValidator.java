package helpers;

/**
 * Created by Spider on 01-Sep-14.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressValidator{

    private Pattern pattern;
    private Matcher matcher;

    private static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String IP_PPORT_PATTERN = "^[0-9]+$";


    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public boolean validateIP(final String ip){
        if (ip.isEmpty())return false;
        pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }


    /**
     * Validate port number with regular expression
     * @param port port number for validation
     * @return true valid port number, false invalid port number i.e 0-65535
     */
    public boolean validatePort(final String port){
        if (port.isEmpty())return false;
        pattern = Pattern.compile(IP_PPORT_PATTERN);
        matcher = pattern.matcher(port);
        if (matcher.matches())
        {
            if (Integer.parseInt(port) <= 65536)
                return true;
        }
        return false;
    }
}
