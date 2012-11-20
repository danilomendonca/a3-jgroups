package a3jg.human;


public class Terminator implements Runnable{

	private HumanNode omino;
	private int i;
	
	public Terminator(HumanNode omino, int i) {
		super();
		this.omino = omino;
		this.i = i;
	}

	@Override
	public void run() {
		
		try {
		
			while (true) {
				
				Thread.sleep(1000);
				int s = (int) (Math.random() * 11);
				int awake = 19000 + (1000 * (s + (i/2)));
				omino.setAtime(awake);
				omino.setStart(System.currentTimeMillis());
				
				int group = (int) (Math.random() * 3);
				if (group == 0) {
					omino.joinGroup("SubRed");
					System.out.println("join subred");
				} else if (group == 1) {
					omino.joinGroup("SubBlue");
					System.out.println("join subblue");
				} else if (group == 2) {
					omino.joinGroup("SubGreen");
					System.out.println("join subgreen");
				} else {
					omino.joinGroup("SubYellow");
					System.out.println("join subyellow");
				}
				
				Thread.sleep(awake);
				
				if(omino.getChannels("SubRed")!=null)
					omino.terminate("SubRed");
				if (omino.getChannels("Red") != null)
					omino.terminate("Red");
				if(omino.getChannels("SubBlue")!=null)
					omino.terminate("SubBlue");
				if (omino.getChannels("Blue") != null)
					omino.terminate("Blue");
				if(omino.getChannels("SubGreen")!=null)
					omino.terminate("SubGreen");
				if (omino.getChannels("Green") != null)
					omino.terminate("Green");
				if(omino.getChannels("SubYellow")!=null)
					omino.terminate("SubYellow");
				if (omino.getChannels("Yellow") != null)
					omino.terminate("Yellow");

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
