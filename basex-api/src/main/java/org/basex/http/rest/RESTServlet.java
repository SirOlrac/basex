package org.basex.http.rest;

import java.io.*;

import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.http.*;
import org.basex.util.http.*;

/**
 * <p>This servlet receives and processes REST requests.</p>
 *
 * @author BaseX Team 2005-16, BSD License
 * @author Christian Gruen
 */
public final class RESTServlet extends BaseXServlet {
  @Override
  protected void run(final HTTPContext http) throws IOException {
    // open database if name was specified
    final RESTSession session = new RESTSession(http, http.context(true));
    final String db = http.db(), path = http.dbpath();
    if(!db.isEmpty()) session.add(new Open(db, path));

    // generate and run commands
    final RESTCmd cmd = command(session);
    try {
      cmd.execute(session.context);
    } catch(final BaseXException ex) {
      // ignore error if code was assigned (same error message)
      if(cmd.code == null) throw ex;
    }

    final HTTPCode code = cmd.code;
    if(code != null) throw code.get(cmd.info());
  }

  /**
   * Creates and returns a REST command.
   * @param session session
   * @return code
   * @throws IOException I/O exception
   */
  private static RESTCmd command(final RESTSession session) throws IOException {
    final String mth = session.http.method;
    if(mth.equals(HttpMethod.GET.name()))    return RESTGet.get(session);
    if(mth.equals(HttpMethod.POST.name()))   return RESTPost.get(session);
    if(mth.equals(HttpMethod.PUT.name()))    return RESTPut.get(session);
    if(mth.equals(HttpMethod.DELETE.name())) return RESTDelete.get(session);
    throw HTTPCode.NOT_IMPLEMENTED_X.get(session.http.req.getMethod());
  }
}
