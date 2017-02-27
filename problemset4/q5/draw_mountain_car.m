function draw_mountain_car(q)

actions = [-1, 1]
state_top = 100

[x, state, absorb] =  mountain_car([0.0 -pi/6], 0)

while (state != state_top)
  plot_mountain_car(x)
  sleep(0.01)
  [max_q, action] = max(q(state, :))
  [x, state, absorb] = mountain_car(x, actions(action))
endwhile

endfunction
