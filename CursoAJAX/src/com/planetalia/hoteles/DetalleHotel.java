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

@WebServlet("/detalleHotel")
public class DetalleHotel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected String serializarComoMatriz(ResultSet rs, String... campos) throws SQLException {
		StringBuilder sb = new StringBuilder();
		boolean primero = true;
		sb.append("[\r\n");
		while (rs.next()) {
			if (primero) primero=false; else sb.append(",");
			sb.append("{");
			for (int i = 0; i < campos.length; i++) {
				sb.append(campos[i]+":'"+rs.getString(campos[i])+"'");
				if (i < campos.length-1) 
					sb.append(",");
			}
			sb.append("}\r\n");
		}
		sb.append("]\r\n");
		return sb.toString();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext ctx = getServletContext();
		Connection con = null;
		ResultSet rs = null;
		try {
			// Obtención y validación de los parámetros
			int id;
			try {
				id = Integer.parseInt(request.getParameter("id"));
			} catch (Exception e) {
				id = 0;
			}

			// Búsqueda del hotel con el id suministrado
			con = DBUtil.getConnection();
			PreparedStatement ps;
			ps = con.prepareStatement("select * from hoteles where hoteles.id = ?");
			ps.setInt(1,id);
			rs = ps.executeQuery();
			
			// Generación del resultado
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			if (!rs.next()) {
				out.println("{}");
				return;
			}
			out.println("{");
			out.println("nombre:'"+rs.getString("nombre")+"',");
			out.println("categoria:"+rs.getString("categoria")+",");
			out.println("direccion:'"+rs.getString("direccion")+"',");
			out.println("poblacion:'"+rs.getString("poblacion")+"',");
			out.println("provincia:'"+rs.getString("provincia")+"',");
			out.println("cp:'"+rs.getString("cp")+"',");
			out.println("precioIndividual:"+rs.getString("precioIndividual")+",");
			out.println("precioDoble:"+rs.getString("precioDoble")+",");
			
			// Recuperar las fotos
			ps = con.prepareStatement("select * from fotos where fotos.id_hotel = ?");
			ps.setInt(1,id);
			rs = ps.executeQuery();
			out.println("fotos : "+serializarComoMatriz(rs, "foto","descripcion")+",");
			
			// Recuperar las características
			ps = con.prepareStatement("select caracteristicas.* from caracteristicas,hoteles_caracteristicas	where " +
					"caracteristicas.id = hoteles_caracteristicas.id_caracteristica and " +
					"hoteles_caracteristicas.id_hotel = ?");
			ps.setInt(1,id);
			rs = ps.executeQuery();
			out.println("caracteristicas : "+serializarComoMatriz(rs, "icono","nombre"));
			out.println("}");
		} catch (Exception e) {
			ctx.log("Error durante la búsqueda de hoteles",e);
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { ctx.log("",e); }
			if (con != null) try { con.close(); }  catch (SQLException e) { ctx.log("",e); } 
		}		
	}

}
