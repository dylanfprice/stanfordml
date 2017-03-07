all_ep_steps = zeros(20, 100);
parfor i = 1:20
  [q, ep_steps] = qlearning(100);
  all_ep_steps(i, :) = ep_steps;
endparfor

plot(mean(all_ep_steps))
