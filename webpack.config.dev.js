const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: "./index.js",
  output: {
    filename: "[name].bundle.js",
    path: path.resolve(__dirname, "dist")
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: {
          loader: "babel-loader",
          options: {
            presets: ["env", "react"],
            plugins: [
              require("babel-plugin-transform-object-rest-spread"),
              require("babel-plugin-transform-class-properties")
            ]
          }
        }
      },
      {
        test: /\.scss$/,
        exclude: /node_modules/,
        loader: "style-loader!css-loader!sass-loader"
      },
      {
        test: /\.css$/,
        loader: "style-loader!css-loader"
      },
      {
        test: /\.(png|jpg|jpeg|gif|svg|tff|woff|woff2)$/,
        loader: "url-loader",
        options: {
          limit: 10000
        }
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.join(__dirname, "index.html")
    })
  ]
};
