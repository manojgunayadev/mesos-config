Mesos Artifacts
===========
This project contains json formats for marathon apps.

1. Inside conf folder contains all marathon app configuration files to start apim and IS nodes.

   gw1 -: Configurations of Gateway Node1
   gw2 -: Configurations of Gateway Node2
   km1 -: Configurations of Key Manager1
   km2 -: Configurations of Key Manager2
   mgtgw1 -: Configurations of Manager Gateway Node1
   mgtgw2 -: Configurations of Manager Gateway Node2
   pubstore1 -: Configurations of Publisher Store Node1
   pubstore2 -: Configurations of Publisher Store Node2
   marathonlb -: Configurations of Marathon Load Balancer. 
   
   Important -: 
   
   Maintains specific configuration jsons for every node.This is done like this, because we need to assign unique service port for each node due to marathon LB sticky sessions limitations.
   After fix this Sticky session issue, you can maintain only four configuration files and use limited number of sepcific service ports.
   
	Description of Environment Variables.
	
	
	    MYSQL_ROOT_PASSWORD	-:	MySql DB User Password. 	
	    APIM_NODE -: This variable indicates type of api manager node type. 
		              Ex-: apimanager:gateway
		               
        MYSQL_PORT_3306_TCP_ADDR -: MySql DB host name. 
        MYSQL_PORT_3306_tcp_PORT -: MySql DB port.        
        MGT_HOST_NAME -: Management host name.
        SUB_CLUSTER_DOMAIN -: This variable indicates manager and worker.
                              
        FACTER_fqdn -: This variable is not using. Can be remove.
        PROFILE_NAME -: API Manager profile name. Give standard profile names. 
        DEPSYNC_SVN_REPO -: SVN Repository 
        SVN_USER -: SVN User Name.
        SVN_PASSWORD -: SVN password.
        MESOS_HOST -: Mesos master host name.
        MESOS_APPID -: Application Id of its own app.
        MESOS_PORT -: Mesos master app.
        MESOS_APPS -: comma separated list of other mesos apps which need to add as members to node.
                      Ex-: For manager node1 need to add mgtgw2, gw1 and gw2.
                      
    Description of Parameters.
    
    	 Pass following parameters to containers to add hosts to /etc/hosts file on containers and add dns entry to /etc/resolv.conf.
    	 
    	 Add the host ip address which runs mesos dns as DNS parameters. If you have multiple mesos dns s running you can have multiple dns parameters.
    	 Add hosts ip addresses which runs nginx. 
    	 
    2. 	nginx Folder
        This folder contains all configuration files related to nginx virtual hosts.
        
    3.  SSL  
        This folder contains all ssl certificates (self signed) for nginx. These certificates should import to client trust store of wso2 servers
    	and need to put /etc/nginx/ssl.
    	           
                              
          