package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ListarVideosProcessadosAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarVideosProcessadosAdapterImpl implements ListarVideosProcessadosAdapter {

    public List<String> listarArquivos() {
        return List.of();
    }
}
