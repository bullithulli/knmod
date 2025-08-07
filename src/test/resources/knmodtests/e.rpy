init python:
  class atl_play_background(object):
    def __init__(self, background):
      super(atl_play_background, self).__init__()
      self.background = background
    def __call__(self, *args, **kwargs):
      if not renpy.predicting():
        renpy.play(self.background, channel="background")