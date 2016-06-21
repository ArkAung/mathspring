package edu.umass.ckc.wo.rest;

import edu.umass.ckc.wo.cache.ProblemMgr;
import edu.umass.ckc.wo.content.Problem;
import edu.umass.ckc.wo.util.Pair;
import edu.umass.ckc.wo.util.TwoTuple;
import org.json.JSONException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.json.JSONObject;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * RESTful web service to provide access to Mathspring resources.
 * Implemented using Jersey framework.
 * Created by david on 6/21/2016.
 */

@Path("/problems")
public class MathspringService {

    // TODO the below URL will be different for rose than localhost.   The value of the variable should come from web.xml
    protected static String tomcatDataSourceURL="jdbc/wodblocal"; // make sure META-INF/context.xml defines this datasource

    // TODO follow instructions from http://jersey.576304.n2.nabble.com/Servlet-Init-For-Jersey-REST-Service-td6507144.html
    // to establish a database connection once for this service.

    @javax.ws.rs.core.Context ServletContext servletContext;

    protected Connection getConnection () throws SQLException {
        // Obtain our environment naming context
        Context initCtx;
        try {
            initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // There is servlet context parameter defined in web.xml that says the name of the database resource to use from context.xml
            String datasource = servletContext.getInitParameter("wodb.datasource");
            // Look up our data source by name
            DataSource ds = (DataSource) envCtx.lookup(datasource);
            Connection conn = ds.getConnection();
            // sometimes after a long time period a connection is returned that is no longer valid
            // and we get an exception.   This will keep requesting connections until a valid one
            // is returned


            return conn;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    @GET
    @Produces("application/json")
    @Path("/ftocservice")
    public Response convertFtoC() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Double fahrenheit = 98.24;
        Double celsius;
        celsius = (fahrenheit - 32)*5/9;
        jsonObject.put("F Value", fahrenheit);
        jsonObject.put("C Value", celsius);

        String result = "@Produces(\"application/json\") Output: \n\nF to C Converter Output: \n\n" + jsonObject;
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/ccss/{std}")
    /**
     * returns JSON for the requested ccss like:  {"ccss":"8.G.2", "numProbs":"5"}
     */
    public Response getCCSSProblems(@PathParam("std")String ccstd) throws JSONException, SQLException {
        JSONObject jsonObject = new JSONObject();
        Connection conn = getConnection();
        int n = ProblemMgr.getStandardNumProblems(conn,ccstd);
        jsonObject.put("ccss", ccstd);
        jsonObject.put("numProbs", n);

//        String result = "@Produces(\"application/json\") Output: \n\n" + jsonObject;
        String result = jsonObject.toString();
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/ccss")
    /**
     * returns JSON for all problems that are CCSS based.
     * {"ccssList":[
     {"ccss":"8.G.2", "numProbs":"5"},
     {"ccss":"6.NS.1", "numProbs":"8"},
     ...
     ]}
     *
     */
    public Response getAllCCSSProblems() throws JSONException, SQLException {
        JSONObject jsonObject = new JSONObject();
        Connection conn = getConnection();
        // get back a list of <CCSS, numProbs>
        List<Pair<String,Integer>> list = ProblemMgr.getAllStandardNumProblems(conn);
        for (Pair<String,Integer> pair : list) {
            String ccss = pair.getP1();
            Integer numProbs = pair.getP2();
            JSONObject rec = new JSONObject();
            rec.put("ccss",ccss);
            rec.put("numProbs",numProbs);
            jsonObject.accumulate("problems",rec);
        }

//        String result = "@Produces(\"application/json\") Output: \n\n" + jsonObject;
        String result = jsonObject.toString();
        return Response.status(200).entity(result).build();
    }

}
