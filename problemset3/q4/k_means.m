function [clusters, centroids] = k_means(X, k)

[m, n] = size(X)

centroids = X(randperm(m, k), :)
clusters = zeros(m, 1)

do
    old_centroids = centroids(:, :)

    % assign clusters
    distances_from_centroids = zeros(m, k)

    for j = 1:k
      centroid = centroids(j, :)
      distances_from_centroids(:, j) = sum((X - centroid) .^ 2, 2)
    endfor

    [values, indices] = min(distances_from_centroids, [], 2)
    clusters = indices

    % compute centroids
    for j = 1:k
        indices = find(clusters == j)
        centroids(j, :) = sum(X(indices, :)) / rows(indices)
    endfor
    
    delta = abs(old_centroids - centroids)

until (delta < .00001)

endfunction
