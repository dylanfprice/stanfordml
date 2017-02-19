function U = pca(X)

X = X'

[u, s, v] = svd(X)

U = v
