mport json
 
class Hello:
    __gui = None
 
    def __init__(self, gui):
        self.__gui = gui
 
    def run(self):
        print 'Hello world!'
 
    def ret_int(self):
        return 123
 
    def ret_string(self):
        return "Test456"
 
    def ret_dict(self):
        thisdict = {
            "brand": "Ford",
            "model": "Mustang",
            "year": "1964"
        }
        return json.dumps(thisdict)
 
    def ret_int2(self, x):
        return 2*x
 