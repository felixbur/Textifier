
/**
 * ClassificationCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

    package test;

    /**
     *  ClassificationCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ClassificationCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ClassificationCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ClassificationCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for ClassificationXMLForText method
            * override this method for handling normal response from ClassificationXMLForText operation
            */
           public void receiveResultClassificationXMLForText(
                    test.ClassificationStub.ClassificationXMLForTextResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from ClassificationXMLForText operation
           */
            public void receiveErrorClassificationXMLForText(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for Version method
            * override this method for handling normal response from Version operation
            */
           public void receiveResultVersion(
                    test.ClassificationStub.VersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from Version operation
           */
            public void receiveErrorVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for ClassificationForText method
            * override this method for handling normal response from ClassificationForText operation
            */
           public void receiveResultClassificationForText(
                    test.ClassificationStub.ClassificationForTextResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from ClassificationForText operation
           */
            public void receiveErrorClassificationForText(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for Description method
            * override this method for handling normal response from Description operation
            */
           public void receiveResultDescription(
                    test.ClassificationStub.DescriptionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from Description operation
           */
            public void receiveErrorDescription(java.lang.Exception e) {
            }
                


    }
    