package com.planetalia.hoteles;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/busquedaHoteles")
public class BusquedaHoteles extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Pausa artificial para comprobar el icono de espera
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			
		}
		ServletContext ctx = getServletContext();
		Connection con = null;
		ResultSet rs = null;
		try {
			// Obtención y validación de los parámetros
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-store");
			int categoria;
			try {
				categoria = Integer.parseInt(request.getParameter("categoria"));
			} catch (Exception e) {
				categoria = 1;
			}
			int tipo;
			tipo = "1".equals(request.getParameter("habitacion"))?1:2;
			double precioMax;
			try {
				precioMax= Double.parseDouble(request.getParameter("pmax"));
			} catch (Exception e) {
				precioMax = 9999;
			}
			double precioMin;
			try {
				precioMin= Double.parseDouble(request.getParameter("pmin"));
			} catch (Exception e) {
				precioMin = 0;
			}


			// Búsqueda de los hoteles
			con = DBUtil.getConnection();
			PreparedStatement ps;
			if ( tipo == 2)
					ps = con.prepareStatement("select id,planox,planoy from hoteles where categoria >= ? and preciodoble between ? and ?");
			else
				ps = con.prepareStatement("select id,planox,planoy from hoteles where categoria >= ? and precioindividual between ? and ?");
			
			ps.setInt(1,categoria);
			ps.setDouble(2,precioMin);
			ps.setDouble(3,precioMax);
			rs = ps.executeQuery();
			
			// Generación del resultado
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			out.print("[");
			boolean primero = true;
			while (rs.next()) {
				if (primero) primero = false; else out.print(",");
				out.println("{ id : "+rs.getString("id")+", planoX:"+rs.getString("planox")+", planoY:"+rs.getString("planoY")+"}");
			}
			out.print("]");
		} catch (Exception e) {
			ctx.log("Error durante la búsqueda de hoteles",e);
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { ctx.log("",e); }
			if (con != null) try { con.close(); }  catch (SQLException e) { ctx.log("",e); } 
		}
	}

}
