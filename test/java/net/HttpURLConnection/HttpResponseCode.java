/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/**
 * @test
 * @bug 4473092
 * @summary Method throws IOException when object should be returned
 */
import java.net.*;
import java.io.*;

public class HttpResponseCode implements Runnable {
    ServerSocket ss;
    /*
     * Our "http" server
     */
    public void run() {
        try {
            Socket s = ss.accept();

            BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()) );
            String req = in.readLine();

            PrintStream out = new PrintStream(
                                 new BufferedOutputStream(
                                    s.getOutputStream() ));


            /* send the header */
            out.print("HTTP/1.1 403 Forbidden\r\n");
            out.print("\r\n");
            out.flush();

            s.close();
            ss.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HttpResponseCode() throws Exception {
        /* start the server */
        ss = new ServerSocket(0);
        (new Thread(this)).start();

        /* establish http connection to server */
        String url = "http://localhost:" +
            Integer.toString(ss.getLocalPort()) +
            "/missing.nothtml";
        URLConnection uc = new URL(url).openConnection();
        int respCode1 = ((HttpURLConnection)uc).getResponseCode();
        ((HttpURLConnection)uc).disconnect();
        int respCode2 = ((HttpURLConnection)uc).getResponseCode();
        if (respCode1 != 403 || respCode2 != 403) {
            throw new RuntimeException("Testing Http response code failed");
        }
    }

    public static void main(String args[]) throws Exception {
        new HttpResponseCode();
    }
}
