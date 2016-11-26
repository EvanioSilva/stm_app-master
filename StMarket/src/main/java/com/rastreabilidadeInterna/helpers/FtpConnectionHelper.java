package com.rastreabilidadeInterna.helpers;

/**
 * Created by Felipe Pereira on 09/03/2015.
 */
public class FtpConnectionHelper {

    private String servidorFtp = "52.204.225.11";
    private String usuarioFtp = "safetrace";
    private String senhaFtp = "9VtivgcVTy0PI";
    private int portaFtp = 21;

    public String getServidorFtp() {
        return servidorFtp;
    }

    public void setServidorFtp(String servidorFtp) {
        this.servidorFtp = servidorFtp;
    }

    public String getUsuarioFtp() {
        return usuarioFtp;
    }

    public void setUsuarioFtp(String usuarioFtp) {
        this.usuarioFtp = usuarioFtp;
    }

    public String getSenhaFtp() {
        return senhaFtp;
    }

    public void setSenhaFtp(String senhaFtp) {
        this.senhaFtp = senhaFtp;
    }

    public int getPortaFtp() {
        return portaFtp;
    }

    public void setPortaFtp(int portaFtp) {
        this.portaFtp = portaFtp;
    }
}
