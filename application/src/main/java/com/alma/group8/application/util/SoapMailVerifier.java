package com.alma.group8.application.util;

import com.alma.group8.api.exceptions.TechnicalException;
import com.cdyne.ws.EmailVerNoTestEmailSoap;
import com.cdyne.ws.ReturnIndicator;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Thibault on 25/11/2016.
 * This class calls a SOAP webservice to verify an email adress
 */
public class SoapMailVerifier implements MailVerifier {

    private EmailVerNoTestEmailSoap verifyEmail;

    private static final Logger LOGGER = Logger.getLogger(MailVerifier.class);

    /**
     * Constructor : Fetch the SOAP WebService
     */
    public SoapMailVerifier() throws TechnicalException {

        try {
            URL url = new URL(CommonVariables.SOAP_URL);
            QName qName = new QName(CommonVariables.SOAP_ROOT, CommonVariables.SOAP_NAME);
            Service service = Service.create(url, qName);
            verifyEmail = service.getPort(EmailVerNoTestEmailSoap.class);
        } catch (MalformedURLException e) {
            throw new TechnicalException("Cannot reach the Soap Verifier");
        }
    }

    /**
     * Ask the SOAP webservice if the email is correct
     * @param email the email to verify
     * @return true if correct, false if not
     */
    @Override
    public boolean isValid(String email) {
        LOGGER.info(String.format("Verifying the email %s", email));
        ReturnIndicator returnIndicator = verifyEmail.verifyEmail(email, "0");
        LOGGER.debug(returnIndicator.getResponseText());
        return returnIndicator.getResponseCode() != 0;
    }

}
