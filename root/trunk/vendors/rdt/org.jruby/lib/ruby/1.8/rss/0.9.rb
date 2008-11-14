require "rss/parser"

module RSS

  module RSS09
    NSPOOL = {}
    ELEMENTS = []

    def self.append_features(klass)
      super
      
      klass.install_must_call_validator('', "")
    end
  end

  class Rss < Element

    include RSS09
    include RootElementMixin

    %w(channel).each do |name|
      install_have_child_element(name, "", nil)
    end

    attr_accessor :rss_version, :version, :encoding, :standalone
    
    def initialize(rss_version, version=nil, encoding=nil, standalone=nil)
      super
    end

    def items
      if @channel
        @channel.items
      else
        []
      end
    end

    def image
      if @channel
        @channel.image
      else
        nil
      end
    end

    def textinput
      if @channel
        @channel.textInput
      else
        nil
      end
    end

    def setup_maker_elements(maker)
      super
      items.each do |item|
        item.setup_maker(maker.items)
      end
    end

    private
    def _attrs
      [
        ["version", true, "rss_version"],
      ]
    end

    class Channel < Element

      include RSS09

      [
        ["title", nil, :text],
        ["link", nil, :text],
        ["description", nil, :text],
        ["language", nil, :text],
        ["copyright", "?", :text],
        ["managingEditor", "?", :text],
        ["webMaster", "?", :text],
        ["rating", "?", :text],
        ["pubDate", "?", :date, :rfc822],
        ["lastBuildDate", "?", :date, :rfc822],
        ["docs", "?", :text],
        ["cloud", "?", :have_attribute],
        ["skipDays", "?", :have_child],
        ["skipHours", "?", :have_child],
        ["image", nil, :have_child],
        ["item", "*", :have_children],
        ["textInput", "?", :have_child],
      ].each do |name, occurs, type, *args|
        __send__("install_#{type}_element", name, "", occurs, name, *args)
      end
      alias date pubDate
      alias date= pubDate=

      private
      def maker_target(maker)
        maker.channel
      end

      def setup_maker_elements(channel)
        super
        [
          [skipDays, "day"],
          [skipHours, "hour"],
        ].each do |skip, key|
          if skip
            skip.__send__("#{key}s").each do |val|
              target_skips = channel.__send__("skip#{key.capitalize}s")
              new_target = target_skips.__send__("new_#{key}")
              new_target.content = val.content
            end
          end
        end
      end

      def not_need_to_call_setup_maker_variables
        %w(image textInput)
      end
    
      class SkipDays < Element
        include RSS09

        [
          ["day", "*"]
        ].each do |name, occurs|
          install_have_children_element(name, "", occurs)
        end

        class Day < Element
          include RSS09

          content_setup

          def initialize(*args)
            if Utils.element_initialize_arguments?(args)
              super
            else
              super()
              self.content = args[0]
            end
          end
      
        end
        
      end
      
      class SkipHours < Element
        include RSS09

        [
          ["hour", "*"]
        ].each do |name, occurs|
          install_have_children_element(name, "", occurs)
        end

        class Hour < Element
          include RSS09

          content_setup(:integer)

          def initialize(*args)
            if Utils.element_initialize_arguments?(args)
              super
            else
              super()
              self.content = args[0]
            end
          end
        end
        
      end
      
      class Image < Element

        include RSS09
        
        %w(url title link).each do |name|
          install_text_element(name, "", nil)
        end
        [
          ["width", :integer],
          ["height", :integer],
          ["description"],
        ].each do |name, type|
          install_text_element(name, "", "?", name, type)
        end

        def initialize(*args)
          if Utils.element_initialize_arguments?(args)
            super
          else
            super()
            self.url = args[0]
            self.title = args[1]
            self.link = args[2]
            self.width = args[3]
            self.height = args[4]
            self.description = args[5]
          end
        end

        private
        def maker_target(maker)
          maker.image
        end
      end

      class Cloud < Element

        include RSS09

        [
          ["domain", "", true],
          ["port", "", true, :integer],
          ["path", "", true],
          ["registerProcedure", "", true],
          ["protocol", "", true],
        ].each do |name, uri, required, type|
          install_get_attribute(name, uri, required, type)
        end

        def initialize(*args)
          if Utils.element_initialize_arguments?(args)
            super
          else
            super()
            self.domain = args[0]
            self.port = args[1]
            self.path = args[2]
            self.registerProcedure = args[3]
            self.protocol = args[4]
          end
        end
      end
      
      class Item < Element
        
        include RSS09

        [
          ["title", '?', :text],
          ["link", '?', :text],
          ["description", '?', :text],
          ["category", '*', :have_children, "categories"],
          ["source", '?', :have_child],
          ["enclosure", '?', :have_child],
        ].each do |tag, occurs, type, *args|
          __send__("install_#{type}_element", tag, "", occurs, tag, *args)
        end

        private
        def maker_target(items)
          if items.respond_to?("items")
            # For backward compatibility
            items = items.items
          end
          items.new_item
        end

        def setup_maker_element(item)
          super
          @enclosure.setup_maker(item) if @enclosure
          @source.setup_maker(item) if @source
        end
        
        class Source < Element

          include RSS09

          [
            ["url", "", true]
          ].each do |name, uri, required|
            install_get_attribute(name, uri, required)
          end
          
          content_setup

          def initialize(*args)
            if Utils.element_initialize_arguments?(args)
              super
            else
              super()
              self.url = args[0]
              self.content = args[1]
            end
          end

          private
          def maker_target(item)
            item.source
          end

          def setup_maker_attributes(source)
            source.url = url
            source.content = content
          end
        end

        class Enclosure < Element

          include RSS09

          [
            ["url", "", true],
            ["length", "", true, :integer],
            ["type", "", true],
          ].each do |name, uri, required, type|
            install_get_attribute(name, uri, required, type)
          end

          def initialize(*args)
            if Utils.element_initialize_arguments?(args)
              super
            else
              super()
              self.url = args[0]
              self.length = args[1]
              self.type = args[2]
            end
          end

          private
          def maker_target(item)
            item.enclosure
          end

          def setup_maker_attributes(enclosure)
            enclosure.url = url
            enclosure.length = length
            enclosure.type = type
          end
        end

        class Category < Element

          include RSS09
          
          [
            ["domain", "", false]
          ].each do |name, uri, required|
            install_get_attribute(name, uri, required)
          end

          content_setup

          def initialize(*args)
            if Utils.element_initialize_arguments?(args)
              super
            else
              super()
              self.domain = args[0]
              self.content = args[1]
            end
          end

          private
          def maker_target(item)
            item.new_category
          end

          def setup_maker_attributes(category)
            category.domain = domain
            category.content = content
          end
          
        end

      end
      
      class TextInput < Element

        include RSS09

        %w(title description name link).each do |name|
          install_text_element(name, "", nil)
        end

        def initialize(*args)
          if Utils.element_initialize_arguments?(args)
            super
          else
            super()
            self.title = args[0]
            self.description = args[1]
            self.name = args[2]
            self.link = args[3]
          end
        end

        private
        def maker_target(maker)
          maker.textinput
        end
      end
      
    end
    
  end

  RSS09::ELEMENTS.each do |name|
    BaseListener.install_get_text_element("", name, "#{name}=")
  end

  module ListenerMixin
    private
    def start_rss(tag_name, prefix, attrs, ns)
      check_ns(tag_name, prefix, ns, "")
      
      @rss = Rss.new(attrs['version'], @version, @encoding, @standalone)
      @rss.do_validate = @do_validate
      @rss.xml_stylesheets = @xml_stylesheets
      @last_element = @rss
      @proc_stack.push Proc.new { |text, tags|
        @rss.validate_for_stream(tags, @ignore_unknown_element) if @do_validate
      }
    end
    
  end

end
