const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  publicPath: './',
  transpileDependencies: true,
  devServer: {
    proxy: {
        '/api': {
            port: 8081,
            target: 'http://localhost:9090',
            changeOrigin: true,
            pathRewrite: {
                '^/api': ''
            }
        }
    }
  }
})
